package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName " // Pesquisa todas as categorias da tabela seller e a coluna name da tabela department(apelidando ele de DepName)(Operacao: Projecao)
					+ "FROM seller " // Pesquisa na tabela seller
					+ "INNER JOIN department " // Junta as duas tabelas (Operacao: Produto cartesiano)
					+ "ON seller.DepartmentId = department.Id " // Filtra apenas os elementos onde a chave estrangeira deparmentID seja igual ao ID da tabela deparment
					+ "WHERE seller.id = ?"); // Filtra a tabela onde o seller.id seja igual ao valor informado
			
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller seller =  instantiateSeller(rs, dep);
				return seller;
			}
			return null;
			
		}catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new Seller();
		seller.setId(rs.getInt("Id"));
		seller.setName(rs.getString("Name"));
		seller.setEmail(rs.getString("Email"));
		seller.setBirthDate(rs.getTimestamp("BirthDate"));  //getDate retorna apernas dd/mm/yyyy, para reuperar a hora, precisa usar getTimestramp
		seller.setBaseSalary(rs.getDouble("BaseSalary"));
		seller.setDepartment(dep);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		// Implementei de outra maneira para testar
		String sql = "SELECT seller.*, department.Name as DepName "
				+ "FROM seller INNER JOIN department "
				+ "ON seller.DepartmentId = department.Id "
				+ "ORDER BY Name asc";
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			try (ResultSet rs = st.executeQuery()) {
				List<Seller> list = new ArrayList<>();
				Map<Integer, Department> map = new HashMap<>();
				
				while (rs.next()) {
					Department dep = map.get(rs.getInt("DepartmentId"));
					
					if (dep == null) {
						dep = instantiateDepartment(rs);
						map.put(rs.getInt("DepartmentId"), dep);
					}
					Seller seller = instantiateSeller(rs, dep);
					list.add(seller);
				}
				return list;
			}
		}catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
					"SELECT seller.*, department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name asc");
			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				Seller seller = instantiateSeller(rs, dep);
				list.add(seller);
			}
			return list;
			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet(rs);
			DB.closeResultSet(rs);
		}
	}
}
