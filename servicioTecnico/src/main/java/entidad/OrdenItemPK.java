/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author javierrojas
 */
@Embeddable
public class OrdenItemPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "orden_idorden", nullable = false)
    private int ordenIdorden;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_iditem", nullable = false)
    private int itemIditem;

    public OrdenItemPK() {
    }

    public OrdenItemPK(int ordenIdorden, int itemIditem) {
        this.ordenIdorden = ordenIdorden;
        this.itemIditem = itemIditem;
    }

    public int getOrdenIdorden() {
        return ordenIdorden;
    }

    public void setOrdenIdorden(int ordenIdorden) {
        this.ordenIdorden = ordenIdorden;
    }

    public int getItemIditem() {
        return itemIditem;
    }

    public void setItemIditem(int itemIditem) {
        this.itemIditem = itemIditem;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) ordenIdorden;
        hash += (int) itemIditem;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdenItemPK)) {
            return false;
        }
        OrdenItemPK other = (OrdenItemPK) object;
        if (this.ordenIdorden != other.ordenIdorden) {
            return false;
        }
        if (this.itemIditem != other.itemIditem) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidad.OrdenItemPK[ ordenIdorden=" + ordenIdorden + ", itemIditem=" + itemIditem + " ]";
    }
    
}
