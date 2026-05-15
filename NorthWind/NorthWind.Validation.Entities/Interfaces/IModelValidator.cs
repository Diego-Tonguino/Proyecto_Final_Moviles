using NorthWind.Validation.Entities.Enums;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Validation.Entities.Interfaces
{
    public interface IModelValidator<T>
    {
        ValidationConstraint Constraint { get; }
        IEnumerable<ValidationError> Errors { get; }
        Task<bool> Validate(T model);
    }
}
