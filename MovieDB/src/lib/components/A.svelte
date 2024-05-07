<script>
  export let props
</script>

<a 
  href={props.link} 
  activeClass="bg-pink-700"
  class="hover:bg-gray-300 dark:hover:bg-gray-700 active:ring-1 
  ring-offset-2 ring-offset-gray-200 dark:ring-offset-gray-800
  ring-blue-500/80 py-1 px-4 rounded-md cursor-pointer"
>
{props.name}
</a>