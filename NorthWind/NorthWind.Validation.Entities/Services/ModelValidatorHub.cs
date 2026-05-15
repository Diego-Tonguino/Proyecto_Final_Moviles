using NorthWind.Validation.Entities.Enums;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Validation.Entities.Services
{
    internal class ModelValidatorHub<ModelType>(
IEnumerable<IModelValidator<ModelType>> validators) :
IModelValidatorHub<ModelType>
    {
        public IEnumerable<ValidationError> Errors { get; private set; }
        public async Task<bool> Validate(ModelType model)
        {
            List<ValidationError> CurrentErrors = [];
            // Obtener los validadores que siempre deben evaluarse.
            var Validators = validators
            .Where(v => v.Constraint == ValidationConstraint.AlwaysValidate)
            .ToList();
            // Agregar los validadores que deben evaluarse cuando no haya errores
            // previos.
            Validators.AddRange(validators
            .Where(v => v.Constraint ==
            ValidationConstraint.ValidateIfThereAreNoPreviousErrors));
            foreach (var Validator in Validators)
            {
                if ((Validator.Constraint == ValidationConstraint.AlwaysValidate) ||
                !CurrentErrors.Any())
                {
                    if (!await Validator.Validate(model))
                    {
                        CurrentErrors.AddRange(Validator.Errors);
                    }
                }
            }
            Errors = CurrentErrors;
            return !Errors.Any();
        }
    }
}
