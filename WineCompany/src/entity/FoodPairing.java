package entity;

import java.util.Objects;

public class FoodPairing {
    private String dishName;
    private String recipe1;
    private String recipe2;
    private String recipe3;
    private String recipe4;
    private String recipe5;

    public FoodPairing(String dishName, String recipe1, String recipe2, String recipe3, String recipe4, String recipe5) {
        this.dishName = dishName;
        this.recipe1 = recipe1;
        this.recipe2 = recipe2;
        this.recipe3 = recipe3;
        this.recipe4 = recipe4;
        this.recipe5 = recipe5;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getRecipe1() {
        return recipe1;
    }

    public void setRecipe1(String recipe1) {
        this.recipe1 = recipe1;
    }

    public String getRecipe2() {
        return recipe2;
    }

    public void setRecipe2(String recipe2) {
        this.recipe2 = recipe2;
    }

    public String getRecipe3() {
        return recipe3;
    }

    public void setRecipe3(String recipe3) {
        this.recipe3 = recipe3;
    }

    public String getRecipe4() {
        return recipe4;
    }

    public void setRecipe4(String recipe4) {
        this.recipe4 = recipe4;
    }

    public String getRecipe5() {
        return recipe5;
    }

    public void setRecipe5(String recipe5) {
        this.recipe5 = recipe5;
    }

    @Override
    public String toString() {
        return "FoodPairing{" +
                "dishName='" + dishName + '\'' +
                ", recipe1='" + recipe1 + '\'' +
                ", recipe2='" + recipe2 + '\'' +
                ", recipe3='" + recipe3 + '\'' +
                ", recipe4='" + recipe4 + '\'' +
                ", recipe5='" + recipe5 + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishName, recipe1, recipe2, recipe3, recipe4, recipe5);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FoodPairing other = (FoodPairing) obj;
        return Objects.equals(dishName, other.dishName) &&
               Objects.equals(recipe1, other.recipe1) &&
               Objects.equals(recipe2, other.recipe2) &&
               Objects.equals(recipe3, other.recipe3) &&
               Objects.equals(recipe4, other.recipe4) &&
               Objects.equals(recipe5, other.recipe5);
    }
}
