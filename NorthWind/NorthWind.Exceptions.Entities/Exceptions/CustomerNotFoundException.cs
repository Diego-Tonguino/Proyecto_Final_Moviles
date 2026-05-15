namespace NorthWind.Exceptions.Entities.Exceptions
{
    /// <summary>
    /// Excepción que se lanza cuando un cliente no existe o fue eliminado
    /// durante el proceso de creación de una orden.
    /// </summary>
    public class CustomerNotFoundException : Exception
    {
        public CustomerNotFoundException() { }
        public CustomerNotFoundException(string message) : base(message) { }
        public CustomerNotFoundException(string message, Exception innerException)
            : base(message, innerException) { }

        /// <summary>
        /// Id del cliente que no fue encontrado
        /// </summary>
        public string CustomerId { get; set; }
    }
}
