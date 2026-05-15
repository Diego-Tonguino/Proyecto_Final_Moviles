using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;

namespace NorthWind.RazorComponents.Validators
{
    public class ModelValidator<T> : ComponentBase
    {
        [CascadingParameter]
        EditContext? EditContext { get; set; }

        [Parameter]
        public required IModelValidatorHub<T> Validator { get; set; }

        ValidationMessageStore? ValidationMessageStore;

        FieldIdentifier GetFieldIdentifier(object model, string propertyName)
        {
            char[] PropertyNameSeparators = new[] { '.', '[' };
            object NewModel = model;
            string PropertyPath = propertyName;
            int SeparatorIndex;
            string Token = null;
            do
            {
                SeparatorIndex =
                PropertyPath.IndexOfAny(PropertyNameSeparators);
                if (SeparatorIndex >= 0)
                {
                    // Extraer la cadena que está antes del
                    // índice encontrado.
                    Token =
                    PropertyPath.Substring(0, SeparatorIndex);
                    // Eliminar la subcadena de la cadena.
                    PropertyPath = PropertyPath
                    .Substring(SeparatorIndex + 1);
                    if (Token.EndsWith("]"))
                    {
                        // Se trata de un índice, p. ej.: "3]"
                        // Extraer el valor del índice.
                        Token = Token.Substring(0, Token.Length - 1);
                        // Extraer la propiedad Item del modelo.
                        var PropertyInfo =
                        NewModel.GetType().GetProperty("Item");
                        // Obtener el tipo del Indexer.
                        var IndexerType =
                        PropertyInfo.GetIndexParameters()[0]
                        .ParameterType;
                        // Obtener el valor del Indexer, p. ej.: 3
                        var IndexerValue = Convert.ChangeType(Token, IndexerType);
                        // Obtener el nuevo modelo.
                        NewModel = PropertyInfo.GetValue(NewModel,
                        new object[] { IndexerValue });
                    }
                    else
                    {
                        // Es una propiedad normal.
                        var PropertyInfo = NewModel.GetType()
                        .GetProperty(Token);
                        NewModel = PropertyInfo.GetValue(NewModel);
                    }
                    Token = null;
                }
            } while (SeparatorIndex >= 0);
            return new FieldIdentifier(NewModel,
            Token ?? PropertyPath);
        }
    
    public void AddErrors(IEnumerable<ValidationError> errors)
        {
            // Eliminar mensajes de validación existentes.
            ValidationMessageStore.Clear();
            // Agregar los errores de validación.
            foreach (var Error in errors)
            {
                var FieldIdentifier =
                GetFieldIdentifier(EditContext.Model, Error.PropertyName);
                ValidationMessageStore.Add(FieldIdentifier, Error.Message);
            }
            EditContext.NotifyValidationStateChanged();
        }
        async void ValidationRequested(object sender,
            ValidationRequestedEventArgs args)
        {
            // Validar el modelo.
            bool IsValid = await Validator.Validate((T)EditContext.Model);
            if (IsValid)
            {
                ValidationMessageStore.Clear();
                EditContext.NotifyValidationStateChanged();
            }
            else
            {
                AddErrors(Validator.Errors);
            }
        }
        async void FieldChanged(object sender, FieldChangedEventArgs e)
        {
            // Eliminamos mensajes de error del campo modificado.
            ValidationMessageStore.Clear(e.FieldIdentifier);
            // Validar el modelo.
            bool IsValid = await Validator.Validate((T)EditContext.Model);
            if (!IsValid)
            {
                foreach (var Item in Validator.Errors)
                {
                    var FieldIdentifier = GetFieldIdentifier(
                    EditContext.Model, Item.PropertyName);
                    // Agregamos únicamente mensajes de error de la propiedad modificada.
                    if (FieldIdentifier.FieldName == e.FieldIdentifier.FieldName &&
                    FieldIdentifier.Model == e.FieldIdentifier.Model)
                    {
                        ValidationMessageStore.Add(FieldIdentifier, Item.Message);
                    }
                }
            }
            EditContext.NotifyValidationStateChanged();
        }
        public override async Task SetParametersAsync(ParameterView parameters)
        {
            // Guardar una referencia de EditContext que tiene los valores
            // originales para poder determinar si hay cambios.
            // EditContext es creado por EditForm cuando EditForm.Model es modificado.
            EditContext PreviousEditContext = EditContext;
            // Establecer el valor de los parámetros, incluyendo el EditContext.
            await base.SetParametersAsync(parameters);
            // Verificar si el EditContext cambió.
            if (EditContext != PreviousEditContext)
            {
                // Crear un nuevo ValidationMessageStore.
                ValidationMessageStore = new ValidationMessageStore(EditContext);
                // Establecer los manejadores de eventos.
                EditContext.OnValidationRequested += ValidationRequested;
                EditContext.OnFieldChanged += FieldChanged;
            }
        }

    }
}

