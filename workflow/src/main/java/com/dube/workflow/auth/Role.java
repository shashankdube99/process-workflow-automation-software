package com.dube.workflow.auth;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(length = 255)
    private String description;

    // --- MANUAL CONSTRUCTORS FOR ECLIPSE ---
    public Role() {}

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    // --- MANUAL GETTERS AND SETTERS FOR ECLIPSE ---
    public UUID getId() { 
        return id; 
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }

    // 🏆 THIS IS THE EXACT METHOD YOUR AUTHCONTROLLER IS BEGGING FOR
    public String getRoleName() { 
        return roleName; 
    }

    public void setRoleName(String roleName) { 
        this.roleName = roleName; 
    }

    public String getDescription() { 
        return description; 
    }

    public void setDescription(String description) { 
        this.description = description; 
    }
}