package com.ombdev.inventorysystemapi.service;

import com.ombdev.inventorysystemapi.exception.InventorySystemException;
import com.ombdev.inventorysystemapi.model.Category;
import com.ombdev.inventorysystemapi.model.ErrorCode;
import com.ombdev.inventorysystemapi.model.SortBy;
import com.ombdev.inventorysystemapi.repository.CategoryRepository;
import com.ombdev.inventorysystemapi.response.DeleteResponse;
import com.ombdev.inventorysystemapi.response.category.CategoryResponse;
import com.ombdev.inventorysystemapi.validator.CategoryValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public Page<CategoryResponse> index(String keyword, int page, int size, SortBy sortBy){
        Page<Category> categoriesPage;

        switch (sortBy) {
            case CATEGORY_CODE_ASC -> categoriesPage = categoryRepository
                    .findAllByCategoryCodeContainingOrCategoryNameContainingOrderByCategoryCodeAsc
                            (keyword, keyword, PageRequest.of(page, size));
            case CATEGORY_CODE_DESC -> categoriesPage = categoryRepository
                    .findAllByCategoryCodeContainingOrCategoryNameContainingOrderByCategoryCodeDesc
                    (keyword, keyword, PageRequest.of(page, size));
            case CATEGORY_NAME_ASC -> categoriesPage = categoryRepository
                    .findAllByCategoryCodeContainingOrCategoryNameContainingOrderByCategoryNameAsc
                            (keyword, keyword, PageRequest.of(page, size));
            case CATEGORY_NAME_DESC -> categoriesPage = categoryRepository
                    .findAllByCategoryCodeContainingOrCategoryNameContainingOrderByCategoryNameDesc
                            (keyword, keyword, PageRequest.of(page, size));
            default -> categoriesPage = categoryRepository
                    .findAllByCategoryCodeContainingOrCategoryNameContainingOrderByIdDesc
                            (keyword, keyword, PageRequest.of(page, size));
        }
        List<CategoryResponse> categories = categoriesPage
                .stream()
                .map(Category::toCategoryResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(categories, PageRequest.of(page, size), categoriesPage.getTotalElements());

    }

    public CategoryResponse store(Category category){
        List<String> errors = CategoryValidator.validate(category);
        if (!errors.isEmpty())
            throw new InventorySystemException("Category is not valid", ErrorCode.CATEGORY_NOT_VALID, errors);
        Optional<Category> categoryTemp = categoryRepository.findByCategoryCode(category.getCategoryCode());
        if (categoryTemp.isPresent())
            throw new InventorySystemException(
                    "this code="+category.getCategoryCode()+" is used with another category, it should be unique!",
                    ErrorCode.CATEGORY_ALREADY_IN_USE);
        return Category.toCategoryResponse(categoryRepository.save(category));
    }

    public CategoryResponse show(Long id){
        if (id == null) throw new InventorySystemException("ID should not be null", ErrorCode.NULL_ID);
        Optional<Category> category = categoryRepository.findById(id);
        category.orElseThrow(() -> new InventorySystemException("Category with ID="+id+" Not Found", ErrorCode.CATEGORY_NOT_FOUND));
        return Category.toCategoryResponse(category.get());
    }

    public CategoryResponse update(Category category){
        List<String> errors = CategoryValidator.validate(category);
        if (!errors.isEmpty())
            throw new InventorySystemException("Category is not valid", ErrorCode.CATEGORY_NOT_VALID, errors);
        return Category.toCategoryResponse(categoryRepository.save(category));
    }

    public DeleteResponse destroy(Long id){
        if (id == null) throw new InventorySystemException("ID should not be null", ErrorCode.NULL_ID);
        categoryRepository.deleteById(id);
        return new DeleteResponse("Category deleted successfully :)");
    }

    public DeleteResponse destroyAll(List<Long> categories){
        categoryRepository.deleteAllById(categories);
        return new DeleteResponse("Selected categories deleted successfully :)");
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(Category::toCategoryResponse)
                .collect(Collectors.toList());
    }
}
