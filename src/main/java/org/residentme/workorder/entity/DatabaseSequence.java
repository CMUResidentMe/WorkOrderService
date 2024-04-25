package org.residentme.workorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;

import lombok.Data;

/**
 * Entity for the database sequence.
 */
@Data
@TypeAlias("sequences")
public class DatabaseSequence {
    @Id
    private String id;
    private long seq;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }
}
