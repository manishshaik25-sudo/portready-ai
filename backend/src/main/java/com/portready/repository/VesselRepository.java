package com.portready.repository;

import com.portready.model.Models.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VesselRepository {

    private final JdbcTemplate jdbc;

    public VesselRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ==================== Vessels ====================

    public List<Vessel> allVessels() {
        String sql = "SELECT * FROM vessels ORDER BY id";
        return jdbc.query(sql, this::mapVessel);
    }

    public Optional<Vessel> vesselById(long id) {
        String sql = "SELECT * FROM vessels WHERE id = ?";
        return jdbc.query(sql, this::mapVessel, id)
                .stream()
                .findFirst();
    }

    // ==================== Certificates ====================

    public List<Certificate> certificates(long vesselId) {
        String sql = "SELECT * FROM certificates WHERE vessel_id = ? ORDER BY expiry_date";
        return jdbc.query(sql, this::mapCertificate, vesselId);
    }

    // ==================== Findings ====================

    public List<Finding> findings(long vesselId) {
        String sql = "SELECT * FROM findings WHERE vessel_id = ? ORDER BY id";
        return jdbc.query(sql, this::mapFinding, vesselId);
    }

    public Optional<Finding> findingById(long id) {
        String sql = "SELECT * FROM findings WHERE id = ?";
        return jdbc.query(sql, this::mapFinding, id)
                .stream()
                .findFirst();
    }

    public void updateFinding(long id, Status status) {
        String sql = "UPDATE findings SET status = ? WHERE id = ?";
        jdbc.update(sql, status.name(), id);
    }

    // ==================== Row Mappers ====================

    private Vessel mapVessel(ResultSet rs, int row) throws SQLException {
        return new Vessel(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("imo_number"),
                rs.getString("destination_port"),
                rs.getDate("arrival_date").toLocalDate()
        );
    }

    private Certificate mapCertificate(ResultSet rs, int row) throws SQLException {
        return new Certificate(
                rs.getLong("id"),
                rs.getLong("vessel_id"),
                rs.getString("certificate_type"),
                rs.getDate("expiry_date").toLocalDate()
        );
    }

    private Finding mapFinding(ResultSet rs, int row) throws SQLException {
        return new Finding(
                rs.getLong("id"),
                rs.getLong("vessel_id"),
                rs.getString("description"),
                rs.getString("status")
        );
    }
}
 