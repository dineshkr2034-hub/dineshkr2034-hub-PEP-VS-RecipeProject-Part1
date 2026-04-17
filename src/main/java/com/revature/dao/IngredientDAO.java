package com.revature.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.revature.util.ConnectionUtil;
import com.revature.util.Page;
import com.revature.util.PageOptions;
import com.revature.model.Ingredient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;



/**
 * The IngredientDAO class handles the CRUD operations for Ingredient objects. It provides methods for creating, retrieving, updating, and deleting Ingredient records from the database. 
 * 
 * This class relies on the ConnectionUtil class for database connectivity and also supports searching and paginating through Ingredient records.
 */

public class IngredientDAO {

    /** A utility class used for establishing connections to the database. */
    @SuppressWarnings("unused")
    private ConnectionUtil connectionUtil;

    /**
     * Constructs an IngredientDAO with the specified ConnectionUtil for database connectivity.
     * 
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param connectionUtil the utility used to connect to the database
     */
    public IngredientDAO(ConnectionUtil connectionUtil) {
        this.connectionUtil = connectionUtil;
    }

    /**
     * TODO: Retrieves an Ingredient record by its unique identifier.
     *
     * @param id the unique identifier of the Ingredient to retrieve.
     * @return the Ingredient object with the specified id.
     */
    public Ingredient getIngredientById(int id) {
        String sql = "SELECT * FROM ingredient WHERE id = ?";
        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSingleRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * TODO: Creates a new Ingredient record in the database.
     *
     * @param ingredient the Ingredient object to be created.
     * @return the unique identifier of the created Ingredient.
     */
    public int createIngredient(Ingredient ingredient) {
        String sql = "INSERT INTO ingredient (name) VALUES (?)";
        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ingredient.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * TODO: Deletes an ingredient record from the database, including references in related tables.
     *
     * @param ingredient the Ingredient object to be deleted.
     */
    public void deleteIngredient(Ingredient ingredient) {
        String sql = "DELETE FROM ingredient WHERE id = ?";
        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ingredient.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: Updates an existing Ingredient record in the database.
     *
     * @param ingredient the Ingredient object containing updated information.
     */
    public void updateIngredient(Ingredient ingredient) {
        String sql = "UPDATE ingredient SET name = ? WHERE id = ?";
        try (Connection conn = connectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ingredient.getName());
            ps.setInt(2, ingredient.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO: Retrieves all ingredient records from the database.
     *
     * @return a list of all Ingredient objects.
     */
    public List<Ingredient> getAllIngredients() {
      String sql = "SELECT * FROM ingredient ORDER BY id"; 
    
    List<Ingredient> ingredients = new ArrayList<>();
    try (Connection conn = connectionUtil.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            ingredients.add(mapSingleRow(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return ingredients;
    }

    /**
     * TODO: Retrieves all ingredient records from the database with pagination options.
     *
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the retrieved ingredients.
     */
    public Page<Ingredient> getAllIngredients(PageOptions pageOptions) {
        String sql = "SELECT * FROM ingredient ORDER BY id LIMIT ? OFFSET ?"; 
    
    List<Ingredient> ingredients = new ArrayList<>();
    int totalElements = getIngredientCount(); 
    int pageSize = pageOptions.getPageSize();
    int totalPages = (int) Math.ceil((double) totalElements / pageSize);
    
    try (Connection conn = connectionUtil.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setInt(1, pageOptions.getPageSize());
        pstmt.setInt(2, pageOptions.getOffset());
        
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ingredients.add(mapSingleRow(rs));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    return new Page<Ingredient>(
        pageOptions.getPageNumber(), 
        pageSize, 
        totalPages, 
        totalElements, 
        ingredients
    );
    }

    

    /**
     * TODO: Searches for Ingredient records by a search term in the name.
     *
     * @param term the search term to filter Ingredient names.
     * @return a list of Ingredient objects that match the search term.
     */
    public List<Ingredient> searchIngredients(String term) {
        String sql = "SELECT * FROM ingredient WHERE name LIKE ? ORDER BY id";
    try (Connection conn = connectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + term + "%");
        try (ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return new ArrayList<>();
    }

    /**
     * TODO: Searches for Ingredient records by a search term in the name with pagination options.
     *
     * @param term the search term to filter Ingredient names.
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the retrieved ingredients.
     */
    // Inside com.revature.dao.IngredientDAO.java

public Page<Ingredient> searchIngredients(String term, PageOptions pageOptions) {
    // FIX: Ensure 'ORDER BY' is placed correctly after 'LIKE ?' and before 'LIMIT'
    String sql = "SELECT * FROM ingredient WHERE name LIKE ? ORDER BY " + 
                 pageOptions.getSortBy() + " " + pageOptions.getSortDirection() + 
                 " LIMIT ? OFFSET ?";
    
    List<Ingredient> ingredients = new ArrayList<>();
    int totalElements = getSearchCount(term); 
    int pageSize = pageOptions.getPageSize();
    
    // Correct totalPages calculation
    int totalPages = (pageSize > 0) ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    
    try (Connection conn = connectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, "%" + term + "%");
        ps.setInt(2, pageSize);
        ps.setInt(3, pageOptions.getOffset());
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ingredients.add(mapSingleRow(rs));
            }
        }
    } catch (SQLException e) {
        // Log the error to see if it's a syntax issue (e.g., "ORDER BYid")
        e.printStackTrace(); 
    }
    
    return new Page<Ingredient>(
        pageOptions.getPageNumber(),
        pageSize,
        totalPages,
        totalElements,
        ingredients
    );
}
private int getSearchCount(String term) {
    String sql = "SELECT COUNT(*) FROM ingredient WHERE name LIKE ?";
    try (Connection conn = connectionUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + term + "%");
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}
    // below are helper methods for your convenience

    /**
     * Maps a single row from the ResultSet to an Ingredient object.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @return an Ingredient object representing the row.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Ingredient mapSingleRow(ResultSet resultSet) throws SQLException {
        return new Ingredient(resultSet.getInt("ID"), resultSet.getString("NAME"));
    }

    /**
     * Maps multiple rows from the ResultSet to a list of Ingredient objects.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @return a list of Ingredient objects.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private List<Ingredient> mapRows(ResultSet resultSet) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        while (resultSet.next()) {
            ingredients.add(mapSingleRow(resultSet));
        }
        return ingredients;
    }
private int getIngredientCount() {
        String sql = "SELECT COUNT(*) FROM ingredient";
        try (Connection conn = connectionUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Paginates the results of a ResultSet into a Page of Ingredient objects.
     *
     * @param resultSet the ResultSet containing Ingredient data.
     * @param pageOptions options for pagination and sorting.
     * @return a Page of Ingredient objects containing the paginated results.
     * @throws SQLException if an error occurs while accessing the ResultSet.
     */
    private Page<Ingredient> pageResults(ResultSet resultSet, PageOptions pageOptions) throws SQLException {
        List<Ingredient> ingredients = mapRows(resultSet);
        
        int totalElements = getIngredientCount(); 
        
        int pageSize = pageOptions.getPageSize();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return new Page<Ingredient>(
            pageOptions.getPageNumber(), 
            pageSize, 
            totalPages, 
            totalElements, 
            ingredients
        );
    }
    
}
