package me.corxl.chatpings.Util;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Pages {

    private PageNode currentPage;

    private final ListIterator<PageNode> pageNodeIterator;

    public Pages(int pageSize, List<TextComponent> componentList) {
        PageNode page = new PageNode(pageSize);
        List<PageNode> pages = new ArrayList<>();
        for (TextComponent tc : componentList) {
            if (!page.isFull()){
                page.addComponent(tc);
            } else {
                pages.add(page);
                page = new PageNode(pageSize);
            }
        }
        pageNodeIterator = pages.listIterator();
        currentPage = pageNodeIterator.next();
    }

    public void nextPage() {
        if (pageNodeIterator.hasNext())
            currentPage = pageNodeIterator.next();
    }

    public void previousPage() {
        if (pageNodeIterator.hasPrevious())
            currentPage = pageNodeIterator.previous();
    }

    public TextComponent displayPageContent() {
        return currentPage.getFullComponent();
    }


    private class PageNode {
        private final List<TextComponent> pageComponents = new ArrayList<>();
        private final int size;
        public PageNode(int size) {
            this.size = size;
        }
        public boolean isFull() {
            return pageComponents.size() >= size;
        }

        public void addComponent(TextComponent component) {
            this.pageComponents.add(component);
        }
        public TextComponent getFullComponent() {
            TextComponent c = Component.empty();
            for (TextComponent component : pageComponents) {
                c = c.append(component).append(Component.newline());
            }
            return c;
        }
    }
}
