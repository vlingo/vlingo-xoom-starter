<script>
	import {
		Button,
		Card,
		CardActions,
		CardText,
		CardTitle,
		Divider,
		Icon,
	} from 'svelte-materialify/src';
	import { mdiPencil } from "@mdi/js";
	import { createEventDispatcher } from "svelte";
	import DeleteWithDialog from "./DeleteWithDialog.svelte";
	import pluralize from 'pluralize';

	const dispatch = createEventDispatcher();

	export let aggregate;
	const methodParameters = (parameters) => {
		return parameters.reduce((acc, cur) => {
			const field = aggregate.stateFields.find(sf => {
				return sf.name === cur.stateField;
			});
			if (field) {
				const bool = cur && (cur.multiplicity === '+' || cur.multiplicity === '-');
				const n = bool ? `${cur.parameterName}` : field.name
				const t = field.collectionType && !bool ? `${field.collectionType}<${field.type}>` : field.type
				acc.push(`${n}: ${t}`);
			}
			return acc;
		}, []).join(', ');
	};

	const fullRoute = (route) => {
		return (
			route.httpMethod + " " +
			aggregate.api.rootPath +
			route.path.replaceAll("*", "")
		).replaceAll("//", "/")  + "->" + route.aggregateMethod;
	};

	const fullReceiver = (receiver) => {
		return receiver.schema.split(":")[3] + "->" + receiver.aggregateMethod;
	};

	const edit = () => dispatch("edit");
	const remove = () => dispatch("remove");
</script>

<Card class="pa-3 ma-3" style="min-width: max-content;height:fit-content;">
	<div class="" style="min-width: 10rem;">
	<div class="mb-2">
		{#each aggregate.api.routes as route}
			<Card class="command mb-1">
				<CardTitle>{fullRoute(route)}</CardTitle>
			</Card>
		{/each}
		{#each aggregate.consumerExchange.receivers as receiver}
		<Card class="event mb-1">
			<CardTitle>{fullReceiver(receiver)}</CardTitle>
		</Card>
		{/each}
	</div>
	<div class="mb-2">
		<Card class="aggregate">
			<CardTitle>
				<div>{aggregate.aggregateName}</div>
			</CardTitle>
			<Divider />
			<CardText>
				{#each aggregate.stateFields as field}
					<div>{field.name}: {field.collectionType ? `${field.collectionType}<${field.type}>` : field.type}</div>
				{/each}
				<Divider />
				{#each aggregate.methods as method}
					<div>{method.name}({methodParameters(method.parameters)})</div>
				{/each}
			</CardText>
		</Card>
	</div>
	{#each aggregate.events as event}
		<Card class="event mb-1">
			<CardTitle>{event.name}</CardTitle>
		</Card>
	{/each}

	<CardActions style="margin-top: auto" class="justify-space-around">
		<Button title="Edit Aggregate" on:click={edit} icon class="ma-2">
			<Icon path={mdiPencil}/>
		</Button>
		<DeleteWithDialog type="Aggregate" on:click={remove} color=""/>
	</CardActions>
</div>
</Card>