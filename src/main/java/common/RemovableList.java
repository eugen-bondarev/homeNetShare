package common;

import java.util.ArrayList;
import java.util.List;

public class RemovableList<T> {
    private List<T> items = new ArrayList<>();
    private List<Integer> indicesToRemove = new ArrayList<>();

    public List<T> getList() {
        return items;
    }

    public void enqueueRemove(int index) {
        indicesToRemove.add(index);
    }

    public void enqueueRemove(T item) {
        int index = items.indexOf(item);
        if (index == -1) return;
        indicesToRemove.add(index);
    }

    public boolean remove() {
        boolean atLeastOneItemRemoved = false;
        for (Integer integer : indicesToRemove) {
            items.remove(integer.intValue());
            atLeastOneItemRemoved = true;
        }
        indicesToRemove.clear();
        return atLeastOneItemRemoved;
    }
}