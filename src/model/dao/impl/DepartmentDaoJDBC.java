package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Department obj) {
		String sql = "INSERT INTO department " // Insere na(INTO) tabela department
				+ "(Name) "
				+ "VALUES "
				+ "(?)";
		try {
			conn.setAutoCommit(false);
			
			PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			
			int rowsAffected = st.executeUpdate(); //Retorna quantidade de linhas alteradas
			
			conn.commit();
			
			if (rowsAffected > 0) {
				try (ResultSet rs = st.getGeneratedKeys()) {
					if (rs.next()) {
						int id = rs.getInt(1);
						obj.setId(id);
					}
				}
			}
			else {
				throw new DbException("Unexpected error! No rows affected");
			}
		}
		catch (SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Insert data rolled back! Cause by: " + e.getMessage());
			}
			catch (SQLException e1) {
				throw new DbException("Error trying to rollback! Caused by: " + e1.getMessage());
			}
		}
	}

	@Override
	public void update(Department obj) {
		String sql = "UPDATE department "
				+ "SET Name = ? "
				+ "WHERE Id = ?";
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.execute();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public void deleteById(Integer id) {
		String sql = "DELETE FROM department "
				+ "WHERE Id = ?";
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			st.execute();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public Department findById(Integer id) {
		String sql = "SELECT department. * "
				+ "FROM department "
				+ "WHERE department.Id = ?";
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, id);
			
			try (ResultSet rs = st.executeQuery()) {
				if (rs.next()) {
					Department dep = instantiateDepartment(rs);
					return dep;
				}
				return null;
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("Id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}

	@Override
	public List<Department> findAll() {
		String sql = "SELECT department.* "
				+ "FROM department "
				+ "ORDER BY Name asc";
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			try (ResultSet rs = st.executeQuery()) {
				List<Department> list = new ArrayList<>();
				
				while (rs.next()) {
					Department department = instantiateDepartment(rs);
					list.add(department);
				}
				return list;
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
}
