package common;

public class IndirectReference<T> {
    private T ref;

    public IndirectReference(T value) {
        ref = value;
    }

    public T get() {
        return ref;
    }

    public void set(T value) {
        ref = value;
    }
}
