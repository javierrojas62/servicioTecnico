/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entidad;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author javierrojas
 */
@Entity
@Table(name = "orden_item", catalog = "bdservicio", schema = "")
@NamedQueries({
    @NamedQuery(name = "OrdenItem.findAll", query = "SELECT o FROM OrdenItem o"),
    @NamedQuery(name = "OrdenItem.findByOrdenIdorden", query = "SELECT o FROM OrdenItem o WHERE o.ordenItemPK.ordenIdorden = :ordenIdorden"),
    @NamedQuery(name = "OrdenItem.findByItemIditem", query = "SELECT o FROM OrdenItem o WHERE o.ordenItemPK.itemIditem = :itemIditem"),
    @NamedQuery(name = "OrdenItem.findByCantidad", query = "SELECT o FROM OrdenItem o WHERE o.cantidad = :cantidad"),
    @NamedQuery(name = "OrdenItem.findByPrecioUnitario", query = "SELECT o FROM OrdenItem o WHERE o.precioUnitario = :precioUnitario")})
public class OrdenItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected OrdenItemPK ordenItemPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cantidad", nullable = false)
    private int cantidad;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    @JoinColumn(name = "item_iditem", referencedColumnName = "iditem", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Item item;
    @JoinColumn(name = "orden_idorden", referencedColumnName = "idorden", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Orden orden;

    public OrdenItem() {
    }

    public OrdenItem(OrdenItemPK ordenItemPK) {
        this.ordenItemPK = ordenItemPK;
    }

    public OrdenItem(OrdenItemPK ordenItemPK, int cantidad, BigDecimal precioUnitario) {
        this.ordenItemPK = ordenItemPK;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public OrdenItem(int ordenIdorden, int itemIditem) {
        this.ordenItemPK = new OrdenItemPK(ordenIdorden, itemIditem);
    }

    public OrdenItemPK getOrdenItemPK() {
        return ordenItemPK;
    }

    public void setOrdenItemPK(OrdenItemPK ordenItemPK) {
        this.ordenItemPK = ordenItemPK;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ordenItemPK != null ? ordenItemPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdenItem)) {
            return false;
        }
        OrdenItem other = (OrdenItem) object;
        if ((this.ordenItemPK == null && other.ordenItemPK != null) || (this.ordenItemPK != null && !this.ordenItemPK.equals(other.ordenItemPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidad.OrdenItem[ ordenItemPK=" + ordenItemPK + " ]";
    }
    
}
