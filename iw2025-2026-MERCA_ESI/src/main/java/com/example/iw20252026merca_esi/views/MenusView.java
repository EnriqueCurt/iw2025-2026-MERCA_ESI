package com.example.iw20252026merca_esi.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.html.H1;

@PageTitle("Los Menús")
@AnonymousAllowed
@Route(value = "menús",  layout = MainLayout.class)
@Menu(title = "los menús")
public class MenusView extends VerticalLayout{

    public MenusView(){
        // Header principal
        //HorizontalLayout header = createHeader();

        H1 titulo = new H1("Los menús");

        // Menú de navegación
        HorizontalLayout menuContent = createMainContent();
        add(titulo, menuContent);
    }

    //aqui se muestra una lista con los objetos de la carta
    private HorizontalLayout createMainContent() {
        HorizontalLayout content = new HorizontalLayout();



        return content;
    }

}
