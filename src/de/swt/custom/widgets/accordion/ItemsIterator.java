package de.swt.custom.widgets.accordion;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

/**
 * Dies ist ein Hilfsobjekt, welches die Navigation über HaccordionItems erleichtern soll.
 * Es wird <b>nur</b> ueber die {@link HAccordionItem} Objekte navigiert.
 * Das {@link HAccordionItemsStack} wird explizit ausgelassen. Es sollte über
 * {@link HAccordion#getStack()} angesprochen werden.
 */
public class ItemsIterator {

    private final List<HAccordionItem> children = new ArrayList<>();

    /**
     * Erstellt ein {@link ItemsIterator}.
     * 
     * @param accordion
     *            - accordion
     */
    public ItemsIterator(HAccordion accordion) {
        // convert the children to the HAccordionItem format
        for (Control child : accordion.getChildren()) {
            if (child instanceof HAccordionItemsStack) {
                continue; // skip stack
            }
            children.add((HAccordionItem) child);
        }
    }

    /**
     * Gibt das Kind des HAccordion zurueck, es kann sich um HAccordionItemStack oder HaccordionItem
     * handeln. Es wird das {@link HAccordionItem} Interface für die Rückgabe benutzt.
     * 
     * @param index
     *            - das Index des angefragten HAccordion Kindes. Das Kind mit dem Index 0 ist das
     *            HAccordionItemStack.
     * @return - das HAccordionItem, mit dem angefragten index. Falls kein Kind mit dem angefragten
     *         Index exisitert - gibt null zurueck.
     */
    HAccordionItem getItem(int index) {
        if (children.size() > index) {
            return children.get(index);
        }
        return null;
    }

    /**
     * Returns the order number of the child, among the accordion's children.
     * 
     * @param item
     * @return the order of the given child if the child was found. Otherwise returns -1.
     */
    int getIndexOf(AnimatableItem item) {
        if (children.contains(item)) {
            return children.indexOf(item);
        }
        return -1;
    }

    public List<HAccordionItem> getChildren() {
        return children;
    }

    /**
     * Return the given part of the child list. The bounds are included: "indexfirst", indexfirst+1,
     * ... ,"indexlast"
     * 
     * @param indexfirst
     * @param indexlast
     * @return Return the given part of the child list if the bounds are given correct. If there is
     *         an out of bounds Exception - returns null.
     */
    Deque<HAccordionItem> getItems(int indexfirst, int indexlast) {
        Deque<HAccordionItem> result = new LinkedList<>();
        try {
            for (int i = indexfirst; i <= indexlast; i++) {
                result.add(children.get(i));
            }
        } catch (IndexOutOfBoundsException e) {
            // for the case, if user asks for index, which is out of bounds
            return new LinkedList<>();
        }
        return result;
    }

    /**
     * Methode gibt HaccordionItems zurueck, welche einen kleineren Index besitzen, als das
     * übergebene Index
     * Es werden nur HaccordionItems zurueckgegeben, nicht der Stack.
     * 
     * @param index
     *            - index
     * @return HaccordionItems, die sich links vom Item mit dem uebergebenen Index befinden.
     */
    public Deque<HAccordionItem> getAllItemsLeftOf(int index) {
        return this.getItems(0, index - 1);
    }

    /**
     * Gibt HAccordionItems zurueck, welche sich links vom übergebenen HAccordionItem
     * befinden sowie das übergebene HAccordionItem.<br>
     * Es werden nur HAccordionItems zurueckgegeben, nicht der Stack.
     * 
     * @param accordionItem
     *            - das Item, auf das Bezug genommen werden soll
     * @return HAccordionItems, die sich links vom übergebenen HAccordionItem befinden
     *         sowie das übergebene HAccordionItem
     */
    public List<HAccordionItem> getAllItemsUpToAndIncluding(HAccordionItem accordionItem) {
        List<HAccordionItem> result = new ArrayList<>();
        for (HAccordionItem item : children) {
            result.add(item);
            if (item == accordionItem) {
                break;
            }
        }
        return result;
    }

    /**
     * Methode gibt HaccordionItems zurueck, welche einen groesseren Index besitzen, als das
     * übergebene Index
     * 
     * @param index
     *            - index
     * @return HaccordionItems, die sich links vom Item mit dem uebergebenen Index befinden.
     */
    public Deque<HAccordionItem> getAllItemsRightOf(int index) {
        return this.getItems(index + 1, children.size() - 1);
    }

    /**
     * Methode gibt HaccordionItems zurueck, welche einen kleineren Index besitzen, als das
     * übergebene Index <b>UND komplett geschlossen sind</b>.
     * Es werden nur HaccordionItems zurueckgegeben, nicht der Stack.
     * 
     * @param index
     *            - index
     * @return HaccordionItems, die sich links vom Item mit dem uebergebenen Index befinden
     *         <b>UND</b> komplett geschlossen sind, also deren Breite=0 ist.
     */
    public Deque<HAccordionItem> getClosedItemsLeftOf(int index) {
        Deque<HAccordionItem> allChildren = this.getItems(0, index - 1);
        Deque<HAccordionItem> result = new LinkedList<>();
        for (HAccordionItem item : allChildren) {
            if (item.getWidth() == 0) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Methode gibt HaccordionItems zurueck, welche einen kleineren Index besitzen, als das
     * übergebene Index <b>UND wenigstens teilweise offen sind</b>.
     * Es werden nur HaccordionItems zurueckgegeben, nicht der Stack.
     * 
     * @param index
     *            - index
     * @return HaccordionItems, die sich links vom Item mit dem uebergebenen Index befinden <b>UND
     *         zumindestens teilweise offen sind, also deren Breite > 0 ist</b>.
     */
    public Deque<HAccordionItem> getOpenItemsLeftOf(int index) {
        Deque<HAccordionItem> allChildren = this.getItems(0, index - 1);
        Deque<HAccordionItem> result = new LinkedList<>();
        for (HAccordionItem item : allChildren) {
            if (item.getWidth() > 0) {
                result.add(item);
            }
        }
        return result;
    }

}
