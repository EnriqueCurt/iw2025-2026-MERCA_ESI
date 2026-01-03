package com.example.iw20252026merca_esi.views;

import com.example.iw20252026merca_esi.components.MenuCard;
import com.example.iw20252026merca_esi.service.MenuService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Menús")
@AnonymousAllowed
@Route(value = "menús", layout = MainLayout.class)
@com.vaadin.flow.router.Menu(title = "Menús")
public class MenusView extends VerticalLayout {

    private final MenuService menuService;

    public MenusView(MenuService menuService) {
        this.menuService = menuService;

        setAlignItems(Alignment.CENTER);
        setWidthFull();

        H1 titulo = new H1("Menús");
        titulo.getStyle()
                .set("text-align", "center")
                .set("margin-bottom", "20px")
                .set("color", "#e30613");
        add(titulo);

        HorizontalLayout menusLayout = new HorizontalLayout();
        menusLayout.setWidthFull();
        menusLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        menusLayout.getStyle().set("flex-wrap", "wrap");

        // Reutilizamos el componente MenuCard
        menuService.listarMenus().forEach(menu -> {
            menusLayout.add(new MenuCard(menu));
        });

        add(menusLayout);
    }
}
