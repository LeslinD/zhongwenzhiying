<script>
  export let props;
  import { onMount } from 'svelte'; 
  import { fade } from 'svelte/transition';
  import { navigating } from '$app/stores';

  let isDropdownOpen = false;
  let movieGenres;
  let dropdownTimeout;

  async function load() {
    const data = await fetch(
      `https://api.themoviedb.org/3/genre/movie/list?api_key=799c0bd0b2baaecc6d9301fadfaea7f7&language=en-US`
    ).then(res => res.json());
    movieGenres = data.genres
  }
  function openDropdown() {
    clearTimeout(dropdownTimeout);
    isDropdownOpen = true;
  }

  function closeDropdown() {
    dropdownTimeout = setTimeout(() => {
      isDropdownOpen = false;
    }, 150); // Adjust the delay as needed
  }
  onMount(() => load())
</script>

<button 
  on:mouseenter={openDropdown}
  on:mouseleave={closeDropdown}
  class="hover:bg-gray-300 dark:hover:bg-gray-700 active:ring-1 
  ring-offset-2 ring-offset-gray-200 dark:ring-offset-gray-800
  ring-blue-500/80 py-1 px-4 rounded-md cursor-pointer"
>
  {props.name}
</button>

{#if isDropdownOpen}
  <div 
    transition:fade={{ duration: 150 }}
    on:mouseenter={openDropdown}
    on:mouseleave={closeDropdown}
    class="py-1 absolute right-24 z-10 mt-2 w-40 origin-top-right rounded-md bg-white shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none" 
    role="menu" aria-orientation="vertical" aria-labelledby="menu-button" tabindex="-1"
  >
    {#each movieGenres as genre}
      <div class="group">
        <a href={`/genre/${genre.id}`}
          on:click={closeDropdown} data-sveltekit-reload
          class="text-black block px-4 py-2 text-base font-medium group-hover:text-blue-500 truncate overflow-hidden" role="menuitem"
        >
          {genre.name}
        </a>
      </div>
    {/each}
  </div>
{/if}
