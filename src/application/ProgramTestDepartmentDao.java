package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class ProgramTestDepartmentDao {

	public static void main(String[] args) {
		
		DepartmentDao departmentDao = DaoFactory.createDepartmentDao();
		
		System.out.println("=== TEST 1: department findById ===");
		Department department = departmentDao.findById(4);
		System.out.println(department);
		
		System.out.println("\n=== TEST 2: department findAll ===");
		List<Department> list = departmentDao.findAll();
		list.forEach(System.out::println);
		
		System.out.println("\n=== TEST 3: department insert ===");
		Department newDepartment = new Department(null, "Tv");
		//departmentDao.insert(newDepartment);
		System.out.println("Inserted! New id: " + newDepartment.getId());
		
		System.out.println("\n=== TEST 4: department update ===");
		department = departmentDao.findById(8);
		department.setName("Mouse");
		departmentDao.update(department);
		System.out.println("Update completed!");
		
		System.out.println("\n=== TEST 5: department delete ===");
		departmentDao.deleteById(6);
		System.out.println("Delete completed!");
	}
	
}
