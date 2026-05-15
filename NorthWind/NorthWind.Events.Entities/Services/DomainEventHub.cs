using NorthWind.Events.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Events.Entities.Services
{
    internal class DomainEventHub<EventType>(
IEnumerable<IDomainEventHandler<EventType>> eventHandlers) :
IDomainEventHub<EventType>
where EventType : IDomainEvent
    {
        public async Task Raise(EventType eventTypeInstance)
        {
            foreach (var Handler in eventHandlers)
            {
                await Handler.Handle(eventTypeInstance);
            }
        }
    }
}
