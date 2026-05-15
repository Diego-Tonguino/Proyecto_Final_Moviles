using FluentValidation;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.ValidationService.FluentValidation
{
    internal class FluentValidationService<T> : IValidationService<T>
    {
        internal readonly AbstractValidatorImplementation<T> Wrapper
            = new AbstractValidatorImplementation<T>();
        public IValidationRules<T, TProperty> AddRuleFor<TProperty>(
        Expression<Func<T, TProperty>> expression) =>
        new ValidationRules<T, TProperty>(Wrapper.RuleFor(expression));
        public ICollectionValidationRules<T, TProperty> AddRuleForEach<TProperty>(
        Expression<Func<T, IEnumerable<TProperty>>> expression) =>
        new CollectionValidationRules<T, TProperty>(
        Wrapper.RuleForEach(expression));
        public async Task<IEnumerable<ValidationError>> Validate(T model)
        {
            var Result = await Wrapper.ValidateAsync(model);
            IEnumerable<ValidationError> Errors = default;
            if (!Result.IsValid)
            {
                Errors = Result.Errors
                .Select(e => new ValidationError(
                e.PropertyName, e.ErrorMessage));
            }
            return Errors;
        }
    }
}
