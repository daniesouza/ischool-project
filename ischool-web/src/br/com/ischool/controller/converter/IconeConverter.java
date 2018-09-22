package br.com.ischool.controller.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.selectonemenu.SelectOneMenu;

import br.com.ischool.util.Icone;

/**
 * @author Daniel Souza de lima e-mail:daniesouza@gmail.com
 *      
 */

@ManagedBean
public class IconeConverter implements Converter {  
  
	
    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        String str = "";
            
        if(arg1 instanceof SelectOneMenu){
        	
        	if(arg2 != null){
            	str = "" + ((Icone) arg2).getImagem();
        	}

        }
        
        return str;
    }
	
    @Override
    public Icone getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {

    	Icone ret = new Icone("","");;
        
    	ret.setImagem(arg2);
        
        return ret;
       
    }

}  