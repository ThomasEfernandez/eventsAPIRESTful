package com.gestion.eventos.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.exceptions.ResourceNotFoundException;
import com.gestion.eventos.api.repository.CategoryRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
   
    @Override
    @Transactional 
    public void deleteById(Long id) {
        
            if (!categoryRepository.existsById(id)) {
                throw new ResourceNotFoundException("Categoria no encontrada con id: " + id);
            }
            categoryRepository.deleteById(id);
    }

    @Override
    @Transactional (readOnly = true)
    public List<Category> findAll() {
       
        return categoryRepository.findAll();
    }

    @Override
    @Transactional (readOnly = true)
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id: " + id));
    }

    @Override
    @Transactional 
    public Category save(Category categoryToSave) {
        return categoryRepository.save(categoryToSave);
    }

    @Override
    @Transactional
    public Category update(Long id, Category categoryToUpdate) {
        Category existingCategory = this.findById(id);
        existingCategory.setName(categoryToUpdate.getName());
        existingCategory.setDescription(categoryToUpdate.getDescription());
        return categoryRepository.save(existingCategory);
    }






}