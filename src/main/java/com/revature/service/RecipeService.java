package com.revature.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.revature.dao.RecipeDAO;
import com.revature.model.Recipe;
import com.revature.util.Page;
import com.revature.util.PageOptions;

/**
 * The RecipeService class provides services related to Recipe objects,
 * including CRUD operations and search functionalities. It acts as an
 * intermediary between the data access layer and the
 * application logic, ensuring that all operations on Recipe objects
 * are handled consistently and efficiently.
 */
public class RecipeService {

    /** The data access object used for performing operations on Recipe entities. */
    private RecipeDAO recipeDAO;

    /**
     * Constructs a RecipeService with the specified RecipeDao.
     *
     * TODO: Finish the implementation so that this class's instance variables are initialized accordingly.
     * 
     * @param recipeDao the RecipeDao to be used by this service for data access
     */
    public RecipeService(RecipeDAO recipeDAO) {
        this.recipeDAO = recipeDAO;
    }

    /**
     * TODO: Finds a Recipe by its unique identifier.
     *
     * @param id the unique identifier of the recipe to be found
     * @return an Optional containing the found Recipe if present;
     *         an empty Optional if not found
     */
    public Optional<Recipe> findRecipe(int id) {
        return Optional.ofNullable(recipeDAO.getRecipeById(id));
    }

    /**
     * TODO: Saves a Recipe object to the data store. If the id is 0, create a new Recipe and set the `recipe` id field to the updated id.
     * 
     * Otherwise, updates the recipe's instructions and chef id.
     *
     * @param recipe the Recipe object to be saved
     */
    public void saveRecipe(Recipe recipe) {
        if (recipe.getId() == 0) {
            int newId = recipeDAO.createRecipe(recipe);
            recipe.setId(newId);
        } else {
            recipeDAO.updateRecipe(recipe);
        }
    }
    

public Recipe getRecipeById(int id) {
    return recipeDAO.getRecipeById(id);
}

public int createRecipe(Recipe recipe) {
    return recipeDAO.createRecipe(recipe);
}

public Recipe updateRecipe(Recipe recipe) {
    recipeDAO.updateRecipe(recipe);
    return recipe;
}
public Page<Recipe> getRecipes(String term, PageOptions pageOptions) {
        if (term == null || term.trim().isEmpty()) {
            return recipeDAO.getAllRecipes(pageOptions); // Calls paginated DAO method
        } else {
            return recipeDAO.searchRecipesByTerm(term, pageOptions); // Calls filtered paginated method
        }
    }

    /**
     * TODO: Searches for recipes with pagination and sorting options.
     *
     * @param term          the search term used to find recipes
     * @param page          the page number to retrieve
     * @param pageSize      the number of recipes per page
     * @param sortBy        the field by which to sort the results
     * @param sortDirection the direction of sorting (ascending or descending)
     * @return a Page containing the results of the search
     */
    public Page<Recipe> searchRecipes(String term, int page, int pageSize, String sortBy, String sortDirection) {
        PageOptions pageOptions = new PageOptions(page, pageSize, sortBy, sortDirection);
    if (term != null && !term.trim().isEmpty()) {
        return recipeDAO.searchRecipesByTerm(term, pageOptions);
    } else {
        return recipeDAO.getAllRecipes(pageOptions);
    }
    }

    /**
     * TODO: Searches for recipes based on a search term.
     *
     * @param term the search term used to find recipes
     * @return a list of Recipe objects that match the search term
     */
    public List<Recipe> searchRecipes(String term) {
        if (term == null || term.isEmpty()) {
            return recipeDAO.getAllRecipes();
        } else {
            return recipeDAO.searchRecipesByTerm(term);
        }
    }

    /**
     * TODO: Deletes a Recipe by its unique identifier.
     *
     * @param id the unique identifier of the recipe to be deleted
     */
    public void deleteRecipe(int id) {
        Recipe recipe = recipeDAO.getRecipeById(id);
        if (recipe != null) {
            recipeDAO.deleteRecipe(recipe);
        }
    }  
    
    public List<Recipe> getAllRecipes(String term) {
    if (term != null && !term.trim().isEmpty()) {
        
        return recipeDAO.searchRecipesByTerm(term);
    } else {
        
        return recipeDAO.getAllRecipes();
    }
}

    
    }

