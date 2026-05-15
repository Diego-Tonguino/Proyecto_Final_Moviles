using FluentValidation;
using NorthWind.Validation.Entities.Abstractions;
using NorthWind.Validation.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.ValidationService.FluentValidation
{
    internal class CollectionValidationRules<T, TProperty>(
 IRuleBuilderInitialCollection<T, TProperty> ruleBuilderInitialCollection) :
 ICollectionValidationRules<T, TProperty>
    {
        public ICollectionValidationRules<T, TProperty> SetValidator(
        IModelValidator<TProperty> modelValidator)
        {
            var ModelValidator =
            modelValidator as AbstractModelValidator<TProperty>;
            var ValidationService =
            ModelValidator.ValidatorService as
            FluentValidationService<TProperty>;
            ruleBuilderInitialCollection
            .SetValidator(ValidationService.Wrapper);
            return this;
        }
    }
}
