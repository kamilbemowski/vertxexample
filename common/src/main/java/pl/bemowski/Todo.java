package pl.bemowski;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kamil Bemowski on 2017-08-28.
 */
public class Todo implements Serializable {

    private static final AtomicInteger acc = new AtomicInteger(0); // counter

    private long id;
    private String title;
    private Boolean completed;
    private Integer order;
    private String url;

    public Todo() {
    }

    public Todo(Todo other) {
        this.id = other.id;
        this.title = other.title;
        this.completed = other.completed;
        this.order = other.order;
        this.url = other.url;
    }

    public static AtomicInteger getAcc() {
        return acc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return id == todo.id &&
                Objects.equals(title, todo.title) &&
                Objects.equals(completed, todo.completed) &&
                Objects.equals(order, todo.order) &&
                Objects.equals(url, todo.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, completed, order, url);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Todo{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", completed=").append(completed);
        sb.append(", order=").append(order);
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
