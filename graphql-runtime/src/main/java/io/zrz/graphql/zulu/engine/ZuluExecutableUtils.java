package io.zrz.graphql.zulu.engine;

import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zrz.graphql.zulu.ZuluUtils;
import io.zrz.graphql.zulu.doc.GQLPreparedSelection;
import io.zrz.graphql.zulu.executable.ExecutableOutputField;

@Deprecated
class ZuluExecutableUtils {

	private static Logger log = LoggerFactory.getLogger(ZuluExecutableUtils.class);

	private static final MethodHandle MH_close = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"close");

	private static final MethodHandle MH_ctor = ZuluUtils
			.sneakyThrows(() -> lookup().findConstructor(ResultWriter.class,
					MethodType.methodType(void.class, ExecutableOutputField.class, ZuluResultReceiver.class)));

	//
	// ------
	//

	private static final MethodHandle MH_write = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class);

	private static final MethodHandle MH_write_object = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class, Object.class);

	private static final MethodHandle MH_write_boolean = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class, Boolean.TYPE);

	private static final MethodHandle MH_write_int = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class, Integer.TYPE);

	private static final MethodHandle MH_write_long = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class, Long.TYPE);

	private static final MethodHandle MH_write_String = ZuluUtils.unreflect(MethodHandles.lookup(), ResultWriter.class,
			"write", ZuluSelection.class, String.class);

	public interface MergeResult {

	}

	public static class ResultWriter {

		private ZuluResultReceiver receiver;

		/**
		 * constructed with the current context.
		 */

		public ResultWriter(ExecutableOutputField ctx, ZuluResultReceiver receiver) {
			log.debug("created result writer for {}: {}", ctx, receiver);
			this.receiver = receiver;
			this.receiver.push(null, null);
		}

		public void write(ZuluSelection sel, Object childval) {
			receiver.write(sel, childval);
		}

		// the scalar types.

		public void write(ZuluSelection field) {
			receiver.write(field);
		}

		public void write(ZuluSelection field, boolean value) {
			receiver.write(field, value);
		}

		public void write(ZuluSelection field, int value) {
			receiver.write(field, value);
		}

		public void write(ZuluSelection field, long value) {
			receiver.write(field, value);
		}

		public void write(ZuluSelection field, String value) {
			receiver.write(field, value);
		}

		/**
		 * the value returned here is passed as the result of the stage.
		 */

		public MergeResult close() {
			this.receiver.pop(null, null);
			return new MergeResult() {
			};
		}

	}

	/**
	 * performs the reduction of the children to a single return value.
	 * 
	 * returns a handle of type (ContextInstance)MergeResult
	 * 
	 * 
	 * @param field
	 * @param sel
	 * @param returnValue
	 * @param childFields
	 * @return
	 */

	private static MethodHandle reduce(ExecutableBuilder exb, ExecutableOutputField field, ZuluSelection sel,
			MethodHandle returnValue, Map<ZuluSelection, MethodHandle> childFields) {

		//
		if (log.isDebugEnabled()) {
			log.debug("reducing {} selection(s) on {}: {}", sel.subselections().size(), field,
					sel.subselections().stream().map(x -> x.fieldName()).collect(Collectors.joining(", ")));
		}

		// the first value is the last call: get return value from result writer, and
		// close it.
		MethodHandle pipeline = createCloseStage();

		// first argument is the writer, drop at the end.
		pipeline = dropArguments(pipeline, 1, ZuluResultReceiver.class, returnValue.type().returnType());

		// calls each of the children
		for (Entry<ZuluSelection, MethodHandle> e : childFields.entrySet()) {

			//
			GQLPreparedSelection subsel = e.getKey();
			MethodHandle handler = e.getValue();

			System.err.println(" -----> " + subsel.path());
			System.err.println(" pipeline = " + pipeline);
			System.err.println(" handler = " + handler);

			// pipeline = foldArguments(
//          pipeline,
//          0,
//          createWriteFolder(subsel, handler, returnValue.type().returnType()));

		}

		// System.err.println(" ----> " + pipeline);

		// construct the result writer for this stage before calling each child.
		// return foldArguments(pipeline, 0, createStageContext(field));

		throw new RuntimeException();

	}

	private static MethodHandle createCloseStage() {
		return MH_close;
	}

	private static MethodHandle createStageContext(ExecutableOutputField field) {
		return insertArguments(MH_ctor, 0, field);
	}

	/**
	 * 
	 */

	/**
	 * generates a {@link MethodHandle} to execute and write the result of this
	 * field to the stage handler.
	 * 
	 * 
	 * 
	 * @param subsel
	 * @param childHandler The handle to invoke to generate the child field value.
	 *                     signature (ZuluResultReceiver, C)T.
	 * @param returnValue
	 * @return a method handle with signature (ResultWriter, ZuluResultsReceiver,
	 *         C)void
	 */

//	private static MethodHandle XXX_createWriteFolder(GQLPreparedSelection subsel, MethodHandle childHandler,
//			Class<?> stageContextType) {
//
//		// -- generate write call for this field.
//		MethodHandle writer = insertArguments(calculateWriteMethod(subsel, childHandler), 1, subsel);
//
//		// --
//		writer = dropArguments(writer, 2, ZuluResultReceiver.class, stageContextType);
//
//		// --
//		writer = foldArguments(writer, 1, childHandler);
//
//		// --
//		return writer;
//
//	}

	/**
	 * provides a handle for processing the result of each field to be reduced.
	 * 
	 * signature must be (ResultWriter,GQLPreparedSelection,T)void
	 * 
	 * @param subsel
	 * @param childHandler
	 * @return
	 */

	private static MethodHandle calculateWriteMethod(GQLPreparedSelection subsel, MethodHandle childHandler) {

		Class<?> childResultType = childHandler.type().returnType();

		if (childResultType.equals(String.class)) {
			return MH_write_String;
		}
		if (childResultType.equals(Long.TYPE)) {
			return MH_write_long;
		}
		if (childResultType.equals(Integer.TYPE)) {
			return MH_write_int;
		}
		if (childResultType.equals(Boolean.TYPE)) {
			return MH_write_boolean;
		}

		// MH which writes null for nulls, or the primitive for others.

		return dropArguments(MH_write, 2, childResultType);

	}

}
