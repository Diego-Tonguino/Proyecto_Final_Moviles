using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;
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
    internal class CreateOrderCustomerValidator(
 IQueriesRepository repository) : IModelValidator<CreateOrderDto>
    {
        readonly List<ValidationError> ErrorsField = [];
        public IEnumerable<ValidationError> Errors =>
        ErrorsField;
        public ValidationConstraint Constraint =>
        ValidationConstraint.ValidateIfThereAreNoPreviousErrors;
        public async Task<bool> Validate(CreateOrderDto model)
        {
            var CurrentBalance = await repository
            .GetCustomerCurrentBalance(model.CustomerId);
            if (CurrentBalance == null)
            {
                ErrorsField.Add(new ValidationError(
                    nameof(model.CustomerId),
CreateOrderMessages.CustomerIdNotFoundError));
            }
            else if (CurrentBalance > 0)
            {
                ErrorsField.Add(new ValidationError(
                nameof(model.CustomerId),
                string.Format(CreateOrderMessages
                .CustomerWithBalanceErrorTemplate,
                model.CustomerId, CurrentBalance)));
            }
            return !ErrorsField.Any();
        }
    }
}
