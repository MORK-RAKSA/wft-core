package com.exception.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Table(name = "system_auth_user")
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class SystemAuthUser extends Auditable<String> {

    @Id
    @Column(nullable = false, updatable = false, length = 100)
    private String username;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private Boolean isActive;

    @Column
    private Boolean isDeleted;

    @Column(length = 500)
    private String remark;

    @Column(length = 20)
    private String referenceId;

    @ManyToOne
    @JoinColumn(name = "line_manager_id")
    private SystemAuthUser lineManagerId;
}
