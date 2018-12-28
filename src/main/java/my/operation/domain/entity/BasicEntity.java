package my.operation.domain.entity;

import java.io.Serializable;

/**
 * Base abstract class for entities which will hold definitions for id.
 */
public abstract class BasicEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
