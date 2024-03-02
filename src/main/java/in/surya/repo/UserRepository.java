package in.surya.repo;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.surya.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Serializable>{
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByEmailAndPassword(String email,String password);
	List<UserEntity> findByNameContaining(String keywords);
}
