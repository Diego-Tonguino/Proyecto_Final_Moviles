namespace NorthWind.Exceptions.Entities.Exceptions
{
    /// <summary>
    /// Excepción que se lanza cuando un producto no existe o fue eliminado
    /// durante el proceso de creación de una orden.
    /// Esto detecta la race condition entre la validación y el guardado.
    /// </summary>
    public class ProductNotFoundException : Exception
    {
        public ProductNotFoundException() { }
        public ProductNotFoundException(string message) : base(message) { }
        public ProductNotFoundException(string message, Exception innerException)
            : base(message, innerException) { }
        
        /// <summary>
        /// ID del producto que no fue encontrado
        /// </summary>
        public int ProductId { get; set; }
    }
}
