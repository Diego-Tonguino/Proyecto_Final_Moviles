using NorthWind.Exceptions.Entities.Exceptions;
using NorthWind.Validation.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.BusinessObjects.Guards
{
    public static class GuardModel
    {
        public static async Task AgainstNotValid<T>(
        IModelValidatorHub<T> modelValidatorHub, T model)
        {
            if (!await modelValidatorHub.Validate(model))
                throw new ValidationException(modelValidatorHub.Errors);
        }
    }
}
