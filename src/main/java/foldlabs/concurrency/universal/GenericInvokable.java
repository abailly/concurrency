package foldlabs.concurrency.universal;

import java.lang.reflect.InvocationTargetException;

public class GenericInvokable<T> implements Invokable{
    private final T target;

    public GenericInvokable(T target) {
        this.target = target;
    }

    @Override
    public Response apply(Request request) {
        try {
            Object invoke = request.method.invoke(target, request.arguments);
            return new Response(Status.OK,invoke);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            return new Response(Status.ERROR,e.getCause());
        }
    }
}
