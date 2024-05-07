<script>
	import { goto } from '$app/navigation';
	import currentNavBar from '$lib/currentNavBar';
	import Header from '$lib/layouts/Header.svelte';
	
	$currentNavBar = Header;

	// Variables bound to respective inputs via bind:value
	let email;
	let password;
	let error;

    const login = () => {
        if (email === 'admin@gmail.com' && password === 'admin') {
			goto('/manage');
        } else {
        	error = 'Invalid email or password';
        }
    };
</script>

<svelte:head>
	<title>Login - Administrator</title>
</svelte:head>

<div class="container mt-4 flex flex-wrap justify-center mx-auto">
	<div
		class="p-4 max-w-sm bg-white rounded-lg border border-gray-200 shadow-md sm:p-6 lg:p-8 dark:bg-gray-800 dark:border-gray-700 w-full"
	>
		<form class="space-y-6" on:submit|preventDefault={login}>
			<div class="heading">
				<a class="back" href="/"><i class="bi bi-arrow-left dark:text-white" /></a>
				<h2 class="text-2xl font-semibold text-black dark:text-white">Administrator Login</h2>
			</div>
			<div>
				<label for="email" class="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300">
                    Email
                </label>
				<input
					type="email"
					required
					name="email"
					placeholder="Enter your email"
					bind:value={email}
					class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white"
				/>
			</div>
			<div>
				<label
					for="password"
					class="block mb-2 text-sm font-medium text-gray-900 dark:text-gray-300"
					>Password</label
				>
				<input
					type="password"
					required
					name="password"
					placeholder="Enter your password"
					bind:value={password}
					class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white"
				/>
			</div>

			{#if error}
                <div class="mt-2 text-red-600 text-sm transition-transform ease-out duration-300 transform">
                    {error}
                </div>
            {/if}

			<button
				type="submit"
				class="w-full text-white bg-blue-500 hover:bg-blue-600 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
			>Log in
            </button>
		</form>
	</div>
</div>
