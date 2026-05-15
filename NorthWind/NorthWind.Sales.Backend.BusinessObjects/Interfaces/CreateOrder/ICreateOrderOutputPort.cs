//using NorthWind.Sales.Backend.BusinessObjects.Aggregates;

namespace NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;

// ************************************
// * OutputPort                       *
// ************************************
// Función: Una vez que el "Interactor" procesa-ejecuta el caso de uso "Crear orden"
//          el "OutputPort" le debe pasar al "Presenter" los datos que este debe
//          transformar-convertir y luego devolver al "Controller" para que algún agente
//          externo los utilice.

//          En el ejemplo:
//          El "OutputPort" una vez que el "Interactor" termine de procesar-ejecutar el caso
//          de uso "Crear orden", le informa al "Presenter" que este debe utilizar el método
//          (Handle) y los datos de la "Orden" recibidos en el parámetro de tipo (OrderAggregate)
//          para que pueda "transformar" los datos al formato requerido por algún agente externo
//          y luego de haberlos transformados debe "devolver" los datos de la "Orden" y el número
//          de la orden (OrderId) creado de solo lectura al "Controller".

//          La función del "Presenter" es la de transformar-convertir y luego debe "devolver"
//          al "Controller" los datos recibidos del "OuputPort", al formato mas conveniente
//          o requeridos por algún agente externo que lo vaya a utilizar o consumir, esto puede
//          ser: una base de datos, aplicación de Consola, una aplicación Web, una aplicación
//          de escritorio (Desktop), una aplicación móvil, una Web API, ect. 
//
//  NOTA: No se necesita un "Presenter:
//        1) Cuando no se necesita datos que devolver.
//        2) O cuando no necesitamos realizar una transformación del resultado devuelto por el
//          "Interactor" .
public interface ICreateOrderOutputPort
{
  int OrderId { get; }

  // NOTA: En el ejemplo el caso de uso únicamente pide que se "notifique" el número de orden
  //       creada.
  //       Pero en este caso se pasa todos los datos de la "orden" creada que están en el
  //       parámetro "OrderAggregate", por lo tanto además del "OrderId" se va regresar los datos
  //       de la "Orden" completa. Y si a futuro se necesitan regresar más información, es aquí
  //       donde se debe indicar cuales son los datos requeridos por algún agente externo para
  //       que estos datos pasen al "Presenter" y este los pueda formatear y luego los devuelva
  //       al "Controller" para que algún agente externo lo pueda utilizar o consumir.

  // En lugar de "Handle-manejar-administrar" se puede poner "Execute-ejecutar"
  Task Handle(OrderAggregate addedOrder);
}
