using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Sales.Validators.Entities.Resources;
using NorthWind.Validation.Entities.Abstractions;
using NorthWind.Validation.Entities.Interfaces;

namespace NorthWind.Sales.Validators.Entities.CreateOrder
{
    internal class CreateOrderDtoValidator :
AbstractModelValidator<CreateOrderDto>
    {
        public CreateOrderDtoValidator(
        IValidationService<CreateOrderDto> validationService,
        IModelValidator<CreateOrderDetailDto> detailValidator) :
        base(validationService)
        {
            AddRuleFor(c => c.CustomerId)
            .NotEmpty(CreateOrderMessages.CustomerIdRequired)
            .Length(5, CreateOrderMessages.CustomerIdRequiredLength);
            AddRuleFor(c => c.ShipAddress)
            .NotEmpty(CreateOrderMessages.ShipAddressRequired)
            .MaximumLength(60, CreateOrderMessages.ShipAddressMaximumLength);
            AddRuleFor(c => c.ShipCity)
            .NotEmpty(CreateOrderMessages.ShipCityRequired)
            .MinimumLength(3, CreateOrderMessages.ShipCityMinimumLength)
            .MaximumLength(15, CreateOrderMessages.ShipCityMaximumLength);
            AddRuleFor(c => c.ShipCountry)
            .NotEmpty(CreateOrderMessages.ShipCountryRequired)
            .MinimumLength(3, CreateOrderMessages.ShipCountryMinimumLength)
            .MaximumLength(15, CreateOrderMessages.ShipCountryMaximumLength);
            AddRuleFor(c => c.ShipPostalCode)
            .MaximumLength(10, CreateOrderMessages.ShipPostalCodeMaximumLength);
            AddRuleFor(c => c.OrderDetails)
            .NotNull(CreateOrderMessages.OrderDetailsRequired)
            .NotEmpty(CreateOrderMessages.OrderDetailsNotEmpty);
            AddRuleForEach(c => c.OrderDetails)
            .SetValidator(detailValidator);
        }
    }
}
