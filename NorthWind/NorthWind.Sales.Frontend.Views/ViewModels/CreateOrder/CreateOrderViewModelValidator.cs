using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Validation.Entities.Abstractions;
using NorthWind.Validation.Entities.Enums;
using NorthWind.Validation.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Frontend.Views.ViewModels.CreateOrder
{
    internal class CreateOrderViewModelValidator(
 IModelValidatorHub<CreateOrderDto> validator) :
 AbstractViewModelValidator<CreateOrderDto, CreateOrderViewModel>(validator,
 ValidationConstraint.AlwaysValidate)
    {
    }
}
