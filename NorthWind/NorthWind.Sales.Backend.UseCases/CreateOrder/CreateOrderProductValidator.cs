using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;
using NorthWind.Sales.Backend.BusinessObjects.ValueObjects;
using NorthWind.Sales.Backend.UseCases.Resources;
using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Validation.Entities.Enums;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.UseCases.CreateOrder
{
    internal class CreateOrderProductValidator(
  IQueriesRepository repository) : IModelValidator<CreateOrderDto>
    {
        readonly List<ValidationError> ErrorsField = [];
        public IEnumerable<ValidationError> Errors =>
        ErrorsField;
        public ValidationConstraint Constraint =>
        ValidationConstraint.ValidateIfThereAreNoPreviousErrors;
        public async Task<bool> Validate(CreateOrderDto model)
        {
            // Obtener los Ids de productos con las cantidades requeridas.
            // Si hay productos repetidos, se suman las cantidades.
            IEnumerable<ProductUnitsInStock> RequiredQuantities =
            model.OrderDetails
            .GroupBy(d => d.ProductId)
            .Select(d => new ProductUnitsInStock(
            d.Key, (short)d.Sum(d => d.Quantity)));
            IEnumerable<int> ProductIds =
            RequiredQuantities.Select(d => d.ProductId);
            // Obtener las existencias en almacén.
            IEnumerable<ProductUnitsInStock> InStockQuantities =
            await repository.GetProductsUnitsInStock(ProductIds);
            // Realizar una operación Lef Join (Uno a muchos) para obtener una
            // colección de elementos:
            // {int ProductId, short Required, short? InStock}.
            // Cada elemento de la colección hará referencia al Id del producto
            // solicitado, la cantidad solicitada y la cantidad disponible
            // en almacén. Si el producto no existe en almacén, la propiedad
            // InStock tendrá null.
            var RequiredVsInStock = RequiredQuantities
            .GroupJoin(InStockQuantities,
            required => required.ProductId, inStock => inStock.ProductId,
            (oneRequired, manyInStock) => new { oneRequired, manyInStock })
            .SelectMany(groupResult =>
            groupResult.manyInStock.DefaultIfEmpty(), // null si no existe el Id
            (groupResult, singleInStock) =>
            new
            {
                groupResult.oneRequired.ProductId,
                Required = groupResult.oneRequired.UnitsInStock,
                InStock = singleInStock?.UnitsInStock
            });
            // Verificar que cada producto exista y que la cantidad
            // en existencia sea mayor o igual a la cantidad requerida.
            foreach (var Item in RequiredVsInStock)
            {
                if (!Item.InStock.HasValue)
                {
                    // Obtener el nombre de la propiedad en el formato:
                    // OrderDetails[x].ProductId
                    string PropertyName =
                    GetPropertyNameWithIndex(model, Item.ProductId,
                    nameof(CreateOrderDetailDto.ProductId));
                    ErrorsField.Add(new ValidationError(PropertyName,
                    string.Format(
                    CreateOrderMessages.ProductIdNotFoundErrorTemplate,
                    Item.ProductId)));
                }
                else
                {
                    if (Item.InStock < Item.Required)
                    {
                        // Obtener el nombre de la propiedad en el formato:
                        // OrderDetails[x].Quantity
                        string PropertyName =
                        GetPropertyNameWithIndex(model, Item.ProductId,
                        nameof(CreateOrderDetailDto.Quantity));
                        ErrorsField.Add(new ValidationError(PropertyName,
                        string.Format(CreateOrderMessages
                        .UnitsInStockLessThanQuantityErrorTemplate,
                        Item.Required, Item.InStock, Item.ProductId)));
                    }
                }
            }
            return !ErrorsField.Any();
        }
        string GetPropertyNameWithIndex(CreateOrderDto model,
        int productId, string propertyName)
        {
            // Localizar el elemento con error de validación.
            var OrderDetail = model.OrderDetails
.First(i => i.ProductId == productId);
            // Obtener su índice.
            var OrderDetaiIndex = model.OrderDetails.ToList()
            .IndexOf(OrderDetail);
            // Devolver el nombre de la propiedad incluyendo su índice.
            return string.Format("{0}[{1}].{2}",
            nameof(model.OrderDetails),
            OrderDetaiIndex,
            propertyName);
        }
    }
}
