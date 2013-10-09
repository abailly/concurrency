package foldlabs.concurrency.universal;

import java.lang.reflect.Method;

/**
 * Generic interface to a <em>sequential object</em>'s operation(s).
 * 
 * @see <b>TAMP</b>, p.127
 */
public interface Invokable {

    /**
     * 
     * @param request the {@link Request} to execute. This object is implicitly the target of the invocation.
     * @return result of invocation. 
     */
    Response apply(Request request);

    /**
     * Encapsulates a request to execute some method of an object.
     */
    class Request {
        final Method method;
        final Object[] arguments;

        public Request(Method method, Object[] arguments) {
            this.method = method;
            this.arguments = arguments;
        }
    }

    /**
     * Encapsulates result of executing a {@link Request}.
     * <p>
     *     The result is either correct termination ({@link Status#OK}) or exception ({@link Status#ERROR}) with a
     *     possibly <tt>null</tt> {@code result} object containing actual result of invocation. This result is the 
     *     {@link Throwable} object thrown if execution terminates with exception.
     * </p>
     */
    class Response {
        final Status status;
        final Object result;

        public Response(Status status, Object result) {
            this.status = status;
            this.result = result;
        }
    }

    enum Status {
        OK,ERROR
    }
}
