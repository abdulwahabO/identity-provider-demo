package me.identityprovider.common.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "DEVELOPERS")
public class Developer {

    // todo: find a way to confiugure a developer as spring security user of dashboard without moving this class there.
    // todo: consider creating a subclass of developer that holds email and password and use it there.
    // todo: this becomes a mappedSuperclass. call the subclass RegisteredDeveloper


    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "developer")
    private List<App> apps;



}
