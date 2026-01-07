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

    @Transactional(readOnly = true)
    public List<Menu> listarMenusConIngredientes() {
        return menuRepository.findAllWithProductosAndIngredientes();
    }

    @Transactional
    public void eliminarMenu(Integer id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menú no encontrado"));
        menu.setEstado(false); // Marcar como inactivo
        menuRepository.save(menu);
    }

    @Transactional(readOnly = true)
    public List<Menu> listarMenusOferta() {
        return menuRepository.findAllWithProductosAndIngredientes().stream()
                .filter(menu -> Boolean.TRUE.equals(menu.getEsOferta()) && menu.getEstado())
                .collect(java.util.stream.Collectors.toList());
    }
}
