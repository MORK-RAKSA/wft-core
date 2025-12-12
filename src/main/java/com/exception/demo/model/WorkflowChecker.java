package com.exception.demo.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "workflow_checker")
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class WorkflowChecker extends Auditable<String> {

    @EmbeddedId
    private ID id;

    @Data
    @Embeddable
    public static class ID {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "username", referencedColumnName = "username", nullable = false, updatable = false)
        private SystemAuthUser user;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "level_id", nullable = false)
        private WorkflowApprovalLevel level;
    }

}
