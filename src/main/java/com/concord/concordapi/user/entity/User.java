package com.concord.concordapi.user.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.concord.concordapi.server.entity.Server;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 4)
    private String name;
    
    @NotBlank
    @Size(min = 4)
    @Column(unique = true)
    @Pattern(regexp = "^[A-Za-z0-9+/=]*$", message = "Username with letters and numbers only")
    private String username;
    
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;
    
    @NotBlank
    private String password;

    private String imagePath;

    @ManyToMany
    @JoinTable(
        name = "user_server", // Nome da tabela de junção
        joinColumns = @JoinColumn(name = "user_id"), // Coluna que referencia o usuário
        inverseJoinColumns = @JoinColumn(name = "server_id") // Coluna que referencia o servidor
    )
    @JsonIgnore
    private List<Server> servers;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "' name='"+name+"', password='"+password+"' email='"+email+"'}";
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, username); // Usando apenas atributos simples
    }

}