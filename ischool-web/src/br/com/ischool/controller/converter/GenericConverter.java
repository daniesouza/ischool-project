package br.com.ischool.controller.converter;

import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.model.DualListModel;

import br.com.ischool.entity.Entidade;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
public class GenericConverter implements Converter {

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        String str = "";
        if (arg2 instanceof Entidade) {
            str = "" + ((Entidade) arg2).getId();
            
            if(arg1 instanceof SelectOneMenu){
            	addAttribute(arg1, (Entidade) arg2);
            }
        }
        return str;
    }
   
   @Override
   public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

       Object ret = null;
       
       if (arg1 instanceof PickList) {
           Object dualList = ((PickList) arg1).getValue();
           DualListModel<?> dl = (DualListModel<?>) dualList;
           for (Object o : dl.getSource()) {
               String id = "";
               if (o instanceof Entidade) {
                   id += ((Entidade) o).getId();
               }

               if (arg2.equals(id)) {
                   ret = o;
                   break;
               }
           }
           if (ret == null)
               for (Object o : dl.getTarget()) {
                       String id = "";
                       if (o instanceof Entidade) {
                           id += ((Entidade) o).getId();
                       }
                   if (arg2.equals(id)) {
                       ret = o;
                       break;
                   }
               }
       }
       
       else if(arg1 instanceof SelectOneMenu){
    	   
    	   ret =  this.getAttributesFrom(arg1).get(arg2);
       }
       
       return ret;
      
   }
   
   protected Map<String, Object> getAttributesFrom(UIComponent component) {
       return component.getAttributes();
   }
   
   protected void addAttribute(UIComponent component, Entidade o) {
       String key = o.getId().toString();
       this.getAttributesFrom(component).put(key, o);
   }
}