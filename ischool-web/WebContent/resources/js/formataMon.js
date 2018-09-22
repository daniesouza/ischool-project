
function formataCasaCentenas(valor){
                                               
    var index = valor.indexOf(',');
    var valorInteiro = valor;
    var valorDecimal = '00';
                                               
    if (index != -1){
        var arrayValor = valor.split(','); 
        valorInteiro = arrayValor[0];
        valorDecimal = arrayValor[1];
    }
                                               
    var tam = valorInteiro.length;
    var valorRetorno = '';
    var tamAux = tam;
                                               
    for (var i = 0 ; i < tam ; i++){
        if (i > 0 && i%3 == 0){
            valorRetorno = valorInteiro.substring(tamAux-1,tamAux) + '.' + valorRetorno;
        }else{
            valorRetorno = valorInteiro.substring(tamAux-1,tamAux) + valorRetorno;
        }
                                                               
        tamAux--;
                                                               
    }
                                               
    return valorRetorno + ',' + valorDecimal;

}

function formataCampoMoeda(obj){

    var valor  = obj.value;
                                               
    while (valor.indexOf('.') != -1){
        valor = valor.replace('.','');
    }
                                               
    if (valor != ''){
                                               
        //var tam = valor.length;
        var index = valor.indexOf(',');
                                                               
        if (index != -1){
                                                               
            var strValor = valor.replace(',','.');
            var valorF = parseFloat(strValor);
                                                                              
            strValor = ''+(Math.round(valorF * 100)/100);
                                                                              
            if (strValor.indexOf('.')==-1){
                strValor = strValor + '.00';
            }
                                                                              
            strValor = strValor.replace('.',',');
                                                                              
            var arrayValor = strValor.split(',');
            var str = arrayValor[1]+'0';
                                                                              
            str = str.substring(0,2);
            strValor = arrayValor[0] + ',' + str;
                                                                              
            var valorRetorno = formataCasaCentenas(strValor);
                                                                              
            obj.value = valorRetorno;
                                                                              
        }else{
                                                                              
            obj.value = formataCasaCentenas(''+valor);
                                                                              
        }
                                               
    }

}
                

