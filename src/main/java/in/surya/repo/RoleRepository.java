package in.surya.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.surya.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Serializable>{

}
