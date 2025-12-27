package com.example.iw20252026merca_esi.service;

import com.example.iw20252026merca_esi.model.Menu;
import com.example.iw20252026merca_esi.repository.MenuRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Menu guardarMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    @Transactional(readOnly = true)
    public List<Menu> listarMenus() {
        return menuRepository.findAllWithProductos();
    }
}
