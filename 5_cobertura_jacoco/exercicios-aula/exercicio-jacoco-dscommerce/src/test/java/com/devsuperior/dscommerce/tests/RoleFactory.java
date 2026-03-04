package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Role;

public class RoleFactory {
    public static Role createRole(){
        return new Role(
                1L,
                "ROLE_ADMIN"
        );
    }
}
