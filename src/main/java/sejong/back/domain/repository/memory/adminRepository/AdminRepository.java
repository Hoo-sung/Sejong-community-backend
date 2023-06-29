package sejong.back.domain.repository.memory.adminRepository;

import java.sql.SQLException;

public interface AdminRepository {
    Boolean AdminCheck(Long id) throws SQLException;
}
