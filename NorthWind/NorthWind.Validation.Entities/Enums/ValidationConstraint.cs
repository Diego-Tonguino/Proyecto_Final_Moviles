using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Validation.Entities.Enums
{
    public enum ValidationConstraint
    {
        AlwaysValidate,
        ValidateIfThereAreNoPreviousErrors
    }
}
