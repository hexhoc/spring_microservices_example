package com.optimagrowth.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@ToString
// HATEOAS. Extends from the RepresentationModel class to inherit the add() method. So once we create a link, we can easily set
// that value to the resource representation without adding any new fields to it.
public class Organization extends RepresentationModel<Organization> {
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;
}
